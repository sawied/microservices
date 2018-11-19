package com.github.sawied.microservice.trade.document;

import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.apache.pdfbox.util.Matrix;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

public class PdfboxDocumentOperator implements DocumentOperator, InitializingBean, SignatureInterface {

	private static final Logger log = LoggerFactory.getLogger(PdfboxDocumentOperator.class);

	private Resource template;

	private Resource keyStoreResource;

	private String storepassword = null;

	private String alias = null;

	private Certificate[] chain;

	private PrivateKey pk;

	private int[] positions = { 45, 565, 253, 64 };
	
	
	
	/**
	 * default sign postiotion is first page,notice ,the index begin with zero.
	 */
	private int signPage = 0;
	
	
	Resource fontResource = null;

	@Override
	public void fillInForm(Map<String, String> data, OutputStream os) throws Exception {
		PDDocument pdf = PDDocument.load(this.template.getInputStream());
		PDAcroForm form = pdf.getDocumentCatalog().getAcroForm();
		form.setNeedAppearances(false);
		
		PDFont font = PDType0Font.load(pdf, fontResource.getInputStream());
		
		//PDFont font = PDType1Font.HELVETICA;
		
		log.debug("the form sheet is XFA :{}",form.hasXFA());
		
		//PDFont font = new PDType1AfmPfbFont(pdf,"cfm.afm");
		if (form != null) {
			List<PDField> fields = form.getFields();
			final String fontName = form.getDefaultResources().add(font).getName();
			
			 String defaultAppearanceString = "/" + fontName + " 10 Tf 0 g";
		        form.setDefaultAppearance(defaultAppearanceString);
			for (int i = 0; i < fields.size(); i++) {
				PDField field = fields.get(i);
				if (field instanceof PDTextField) {
					log.info("partial field {},fully Qualified filed name {}", field.getPartialName(),
							field.getFullyQualifiedName());
					PDTextField textField = (PDTextField) field;
					textField.setDefaultAppearance("/" + fontName + " 10 Tf 0 g");
					textField.setValue("郑");
					
				}
				if(field instanceof PDCheckBox && !field.getFullyQualifiedName().startsWith("Check Box")) {
					field.setValue("Yes");
				}
			}
			form.flatten();
		}
		pdf.save(os);
		pdf.close();
		os.close();
	}

	/**
	 * 
	 */
	@Override
	public void esignature(Resource resource, Resource imageResource, OutputStream os) {
		PDSignature signature = null;
		PDRectangle rect = null;
		try {
			PDDocument doc = PDDocument.load(resource.getInputStream());
			int accessPermissions = PdfboxDocumentOperator.getMDPPermission(doc);
			if (accessPermissions == 1) {
				throw new IllegalStateException(
						"No changes to the document are permitted due to DocMDP transform parameters dictionary");
			}

			signature = new PDSignature();
			rect = createSignatureRectangle(doc,
					new Rectangle2D.Float(positions[0], positions[1], positions[2], positions[3]));

			if (doc.getVersion() >= 1.5f && accessPermissions == 0) {
				PdfboxDocumentOperator.setMDPPermission(doc, signature, 2);
			}
			
			 PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm();

	        if (acroForm != null && acroForm.getNeedAppearances())
	        {
	            // PDFBOX-3738 NeedAppearances true results in visible signature becoming invisible 
	            // with Adobe Reader
	            if (acroForm.getFields().isEmpty())
	            {
	                // we can safely delete it if there are no fields
	                acroForm.getCOSObject().removeItem(COSName.NEED_APPEARANCES);
	                // note that if you've set MDP permissions, the removal of this item
	                // may result in Adobe Reader claiming that the document has been changed.
	                // and/or that field content won't be displayed properly.
	                // ==> decide what you prefer and adjust your code accordingly.
	            }
	            else
	            {
	                System.out.println("/NeedAppearances is set, signature may be ignored by Adobe Reader");
	            }
	        }

			// default filter
			signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);

			// subfilter for basic and PAdES Part 2 signatures
			signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);

			signature.setName("Name");
			signature.setLocation("Location");
			signature.setReason("Reason");

			// the signing date, needed for valid signature
			signature.setSignDate(Calendar.getInstance());
			
			SignatureInterface signatureInterface = this;
			
			
			// register signature dictionary and sign interface
			SignatureOptions signatureOptions = new SignatureOptions();
			signatureOptions.setVisualSignature(createVisualSignatureTemplate(doc, this.signPage, rect,imageResource));
			signatureOptions.setPage(this.signPage);
			doc.addSignature(signature, signatureInterface, signatureOptions);
			
			 doc.saveIncremental(os);
			 doc.close();
			 IOUtils.closeQuietly(signatureOptions);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private InputStream createVisualSignatureTemplate(PDDocument srcDoc, int pageNum, PDRectangle rect,Resource image)
			throws IOException {
		PDDocument doc = new PDDocument();
		PDPage page = new PDPage(srcDoc.getPage(pageNum).getMediaBox());
		doc.addPage(page);

		PDAcroForm acroForm = new PDAcroForm(doc);
		doc.getDocumentCatalog().setAcroForm(acroForm);
		PDSignatureField signatureField = new PDSignatureField(acroForm);
		PDAnnotationWidget widget = signatureField.getWidgets().get(0);
		List<PDField> acroFormFields = acroForm.getFields();
		acroForm.setSignaturesExist(true);
		acroForm.setAppendOnly(true);
		acroForm.getCOSObject().setDirect(true);
		acroFormFields.add(signatureField);

		widget.setRectangle(rect);

		// from PDVisualSigBuilder.createHolderForm()
		PDStream stream = new PDStream(doc);
		PDFormXObject form = new PDFormXObject(stream);
		PDResources res = new PDResources();
		form.setResources(res);
		form.setFormType(1);
		PDRectangle bbox = new PDRectangle(rect.getWidth(), rect.getHeight());
		
		form.setBBox(bbox);
        
        // from PDVisualSigBuilder.createAppearanceDictionary()
        PDAppearanceDictionary appearance = new PDAppearanceDictionary();
        appearance.getCOSObject().setDirect(true);
        PDAppearanceStream appearanceStream = new PDAppearanceStream(form.getCOSObject());
        appearance.setNormalAppearance(appearanceStream);
        widget.setAppearance(appearance);

        PDPageContentStream cs = new PDPageContentStream(doc, appearanceStream);
        // show background (just for debugging, to see the rect size + position)
        //cs.setNonStrokingColor(Color.WHITE);
        //cs.addRect(-5000, -5000, 10000, 10000);
        //cs.fill();
        
        
        // show background image
        // save and restore graphics if the image is too large and needs to be scaled
        cs.saveGraphicsState();
        cs.transform(Matrix.getScaleInstance(0.25f, 0.25f));
        PDImageXObject img = PDImageXObject.createFromFileByExtension(image.getFile(), doc);
        cs.drawImage(img, 0, 0);
        cs.restoreGraphicsState();

//        // show text
//        float fontSize = 10;
//        float leading = fontSize * 1.5f;
//        cs.beginText();
//        cs.setFont(font, fontSize);
//        cs.setNonStrokingColor(Color.black);
//        cs.newLineAtOffset(fontSize, height - leading);
//        cs.setLeading(leading);
//        cs.showText("(Signature very wide line 1)");
//        cs.newLine();
//        cs.showText("(Signature very wide line 2)");
//        cs.newLine();
//        cs.showText("(Signature very wide line 3)");
//        cs.endText();

        cs.close();

        // no need to set annotations and /P entry
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        doc.save(baos);
        doc.close();
        return new ByteArrayInputStream(baos.toByteArray());

	}

	private PDRectangle createSignatureRectangle(PDDocument doc, Rectangle2D humanRect) {
		float x = (float) humanRect.getX();
		float y = (float) humanRect.getY();
		float width = (float) humanRect.getWidth();
		float height = (float) humanRect.getHeight();
		PDPage page = doc.getPage(0);
		PDRectangle pageRect = page.getCropBox();
		PDRectangle rect = new PDRectangle();

		rect.setLowerLeftX(x);
		rect.setUpperRightX(x + width);
		rect.setLowerLeftY(pageRect.getHeight() - y - height);
		rect.setUpperRightY(pageRect.getHeight() - y);
		return rect;
	}

	/**
	 * Get the access permissions granted for this document in the DocMDP transform
	 * parameters dictionary. Details are described in the table "Entries in the
	 * DocMDP transform parameters dictionary" in the PDF specification.
	 *
	 * @param doc document.
	 * @return the permission value. 0 means no DocMDP transform parameters
	 *         dictionary exists. Other return values are 1, 2 or 3. 2 is also
	 *         returned if the DocMDP transform parameters dictionary is found but
	 *         did not contain a /P entry, or if the value is outside the valid
	 *         range.
	 */
	public static int getMDPPermission(PDDocument doc) {
		COSBase base = doc.getDocumentCatalog().getCOSObject().getDictionaryObject(COSName.PERMS);
		if (base instanceof COSDictionary) {
			COSDictionary permsDict = (COSDictionary) base;
			base = permsDict.getDictionaryObject(COSName.DOCMDP);
			if (base instanceof COSDictionary) {
				COSDictionary signatureDict = (COSDictionary) base;
				base = signatureDict.getDictionaryObject("Reference");
				if (base instanceof COSArray) {
					COSArray refArray = (COSArray) base;
					for (int i = 0; i < refArray.size(); ++i) {
						base = refArray.getObject(i);
						if (base instanceof COSDictionary) {
							COSDictionary sigRefDict = (COSDictionary) base;
							if (COSName.DOCMDP.equals(sigRefDict.getDictionaryObject("TransformMethod"))) {
								base = sigRefDict.getDictionaryObject("TransformParams");
								if (base instanceof COSDictionary) {
									COSDictionary transformDict = (COSDictionary) base;
									int accessPermissions = transformDict.getInt(COSName.P, 2);
									if (accessPermissions < 1 || accessPermissions > 3) {
										accessPermissions = 2;
									}
									return accessPermissions;
								}
							}
						}
					}
				}
			}
		}
		return 0;
	}

	/**
	 * Set the access permissions granted for this document in the DocMDP transform
	 * parameters dictionary. Details are described in the table "Entries in the
	 * DocMDP transform parameters dictionary" in the PDF specification.
	 *
	 * @param doc               The document.
	 * @param signature         The signature object.
	 * @param accessPermissions The permission value (1, 2 or 3).
	 */
	static public void setMDPPermission(PDDocument doc, PDSignature signature, int accessPermissions) {
		COSDictionary sigDict = signature.getCOSObject();

		// DocMDP specific stuff
		COSDictionary transformParameters = new COSDictionary();
		transformParameters.setItem(COSName.TYPE, COSName.getPDFName("TransformParams"));
		transformParameters.setInt(COSName.P, accessPermissions);
		transformParameters.setName(COSName.V, "1.2");
		transformParameters.setNeedToBeUpdated(true);

		COSDictionary referenceDict = new COSDictionary();
		referenceDict.setItem(COSName.TYPE, COSName.getPDFName("SigRef"));
		referenceDict.setItem("TransformMethod", COSName.DOCMDP);
		referenceDict.setItem("DigestMethod", COSName.getPDFName("SHA1"));
		referenceDict.setItem("TransformParams", transformParameters);
		referenceDict.setNeedToBeUpdated(true);

		COSArray referenceArray = new COSArray();
		referenceArray.add(referenceDict);
		sigDict.setItem("Reference", referenceArray);
		referenceArray.setNeedToBeUpdated(true);

		// Catalog
		COSDictionary catalogDict = doc.getDocumentCatalog().getCOSObject();
		COSDictionary permsDict = new COSDictionary();
		catalogDict.setItem(COSName.PERMS, permsDict);
		permsDict.setItem(COSName.DOCMDP, signature);
		catalogDict.setNeedToBeUpdated(true);
		permsDict.setNeedToBeUpdated(true);
	}
	
	/**
	 * sign the pdf
	 */
	 public byte[] sign(InputStream content) throws IOException{
		// cannot be done private (interface)
	        try
	        {
	            List<Certificate> certList = Arrays.asList(this.chain);
	            Store<?> certs = new JcaCertStore(certList);
	            CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
	            org.bouncycastle.asn1.x509.Certificate cert = org.bouncycastle.asn1.x509.Certificate.getInstance(this.chain[0].getEncoded());
	            ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA256WithRSA").build(this.pk);
	            gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().build()).build(sha1Signer, new X509CertificateHolder(cert)));
	            
	            gen.addCertificates(certs);
	            CMSProcessableInputStream msg = new CMSProcessableInputStream(content);
	            CMSSignedData signedData = gen.generate(msg, false);
	          
	            return signedData.getEncoded();
	        }
	        catch (GeneralSecurityException e)
	        {
	            throw new IOException(e);
	        }
	        catch (CMSException e)
	        {
	            throw new IOException(e);
	        }
	        catch (OperatorCreationException e)
	        {
	            throw new IOException(e);
	        } 
	 }
	
	

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			KeyStore keystore = KeyStore.getInstance("JKS");
			char[] password = storepassword.toCharArray();
			keystore.load(keyStoreResource.getInputStream(), password);
			this.chain = keystore.getCertificateChain(alias);
			this.pk = (PrivateKey) keystore.getKey(alias, password);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
				| UnrecoverableKeyException e) {
			log.error("parse keystore failed,please try config", e);
			throw e;
		}
	}

	public void setTemplate(Resource template) {
		this.template = template;
	}

	public void setKeyStoreResource(Resource keyStoreResource) {
		this.keyStoreResource = keyStoreResource;
	}

	public void setStorepassword(String storepassword) {
		this.storepassword = storepassword;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setChain(Certificate[] chain) {
		this.chain = chain;
	}

	public int getSignPage() {
		return signPage;
	}

	public void setSignPage(int signPage) {
		this.signPage = signPage;
	}

	public void setPositions(int[] positions) {
		this.positions = positions;
	}

	public void setFontResource(Resource fontResource) {
		this.fontResource = fontResource;
	}
	
}
