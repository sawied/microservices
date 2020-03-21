package com.github.sawied.microservice.trade.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.notification.EventType;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.property.MapiPropertyType;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.SortDirection;
import microsoft.exchange.webservices.data.core.enumeration.service.ConflictResolutionMode;
import microsoft.exchange.webservices.data.core.enumeration.service.MessageDisposition;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.notification.NotificationEventArgs;
import microsoft.exchange.webservices.data.notification.StreamingSubscription;
import microsoft.exchange.webservices.data.notification.StreamingSubscriptionConnection;
import microsoft.exchange.webservices.data.notification.StreamingSubscriptionConnection.INotificationEventDelegate;
import microsoft.exchange.webservices.data.notification.StreamingSubscriptionConnection.ISubscriptionErrorDelegate;
import microsoft.exchange.webservices.data.notification.SubscriptionErrorEventArgs;
import microsoft.exchange.webservices.data.property.complex.AttachmentCollection;
import microsoft.exchange.webservices.data.property.complex.ExtendedProperty;
import microsoft.exchange.webservices.data.property.complex.ExtendedPropertyCollection;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.definition.ExtendedPropertyDefinition;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;

public class ExchangeAPI {
	
	private static final UUID uuid = UUID.fromString("01638372-9F96-43b2-A403-B504ED14A910");

	@Autowired
	private ExchangeService service;

	public void sendMessage() {
		EmailMessage msg;
		try {
			msg = new EmailMessage(service);
			msg.setSubject("Hello EWS_T world!");
			msg.setExtendedProperty(new ExtendedPropertyDefinition(uuid,"case-management", MapiPropertyType.Integer), 1);
			msg.setBody(MessageBody.getMessageBodyFromText("Sent using the EWS Java API."));
			msg.getToRecipients().add("danan.2009@hotmail.com");
			msg.send();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void startup() {
		List<FolderId> folder = new ArrayList<FolderId>();
		folder.add(FolderId.getFolderIdFromWellKnownFolderName(WellKnownFolderName.Inbox));
		StreamingSubscriptionConnection connection = null;
		try {
			StreamingSubscription subscribeToStreamingNotifications = service.subscribeToStreamingNotifications(folder,
					EventType.NewMail);
			connection = new StreamingSubscriptionConnection(service, 30);
			connection.addSubscription(subscribeToStreamingNotifications);
			// if the event arrive
			connection.addOnNotificationEvent(new INotificationEventDelegate() {

				@Override
				public void notificationEventDelegate(Object sender, NotificationEventArgs args) {

					System.out.println("the event arrived.");

				}
			});
			// if the event arrive
			connection.addOnDisconnect(new ISubscriptionErrorDelegate() {

				@Override
				public void subscriptionErrorDelegate(Object sender, SubscriptionErrorEventArgs args) {

					System.out.println("the disconnection event.");
				}
			});

			connection.open();

		} catch (Exception e) {
			e.fillInStackTrace();
		} finally {
			connection.close();
		}

	}

	/**
	 * retrieve mail list and mark with extended property
	 * @return
	 */
	public List<String> markMailList(){
	
		// fetch mail list
		try {
			
			ItemView view = new ItemView(2);
			FindItemsResults<Item> findResults;
			ExtendedPropertyDefinition cmProperty = new ExtendedPropertyDefinition(uuid,"case-management", MapiPropertyType.Integer);
			view.getOrderBy().add(ItemSchema.DateTimeReceived, SortDirection.Descending);
			view.setPropertySet(new PropertySet(BasePropertySet.IdOnly, cmProperty,ItemSchema.Subject, ItemSchema.DateTimeReceived));
				findResults = service.findItems(WellKnownFolderName.Inbox,view);
				service.loadPropertiesForItems(findResults, PropertySet.FirstClassProperties);
				
				for(Item item : findResults.getItems())
				{
					// Any process Email
					if (item instanceof EmailMessage) {
						EmailMessage email  = (EmailMessage) item;
						email.setExtendedProperty(cmProperty, 1);
						service.updateItem(item, FolderId.getFolderIdFromWellKnownFolderName(WellKnownFolderName.Inbox), ConflictResolutionMode.AutoResolve, MessageDisposition.SaveOnly, null);
					}							
				}
				
		}catch(Exception ex) {
			ex.fillInStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<String> retrieveMarkedMailList(){
		
		// fetch mail list
		try {
			
			ItemView view = new ItemView(2);
			FindItemsResults<Item> findResults;
			ExtendedPropertyDefinition cmProperty = new ExtendedPropertyDefinition(uuid,"case-management", MapiPropertyType.Integer);
			view.getOrderBy().add(ItemSchema.DateTimeReceived, SortDirection.Descending);
			
			PropertySet propertySet = new PropertySet(BasePropertySet.FirstClassProperties,cmProperty);
			view.setPropertySet(propertySet);
			
			//new SearchFilter.Not(new ExtendedPropertyDefinition(0x00000003, MapiPropertyType.Integer));
			   SearchFilter searchFilter = new SearchFilter.IsEqualTo(cmProperty, 1);
	
				findResults = service.findItems(WellKnownFolderName.Inbox,searchFilter, 
						view);
				service.loadPropertiesForItems(findResults, propertySet);
				
				for(Item item : findResults.getItems())
				{
					// Any process Email
					if (item instanceof EmailMessage) {
						EmailMessage email  = (EmailMessage) item;
						ExtendedPropertyCollection extendedProperties = email.getExtendedProperties();
						int count = extendedProperties.getCount();
						System.out.println("extend property size :" + count);
						
						List<ExtendedProperty> items = extendedProperties.getItems();
						for(ExtendedProperty property : items) {
							System.out.println("property name :" + property.getPropertyDefinition().getName());
							System.out.println("property value :" + property.getValue());
						}
						
						System.out.println(email.getSubject());
						String from = email.getFrom().getAddress();
						AttachmentCollection attachments = email.getAttachments();
						int size = email.getSize();
						Date dateTimeCreated = email.getDateTimeCreated();
						Date dateTimeReceived = email.getDateTimeReceived();
						Date dateTimeSent = email.getDateTimeSent();
						String displayTo = email.getDisplayTo();
						MessageBody body = email.getBody();
						body.setBodyType(BodyType.Text);
						BodyType type=body.getBodyType();
						System.out.println(body.toString());
						//email.save();
					}		
					
				}
				
		}catch(Exception ex) {
			ex.fillInStackTrace();
		}
		
		return null;
	}

}
