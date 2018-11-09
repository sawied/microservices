
import store from 'store';

//define services list of this function
let host = "/apis";


const services=Object.assign(
    
    { 'info':{
       endpoint:host+"/oauth2/info",
       method:'GET',
       headers:{'Content-Type': 'application/json'},
       options:{timeout:3000}
     },
     "auth":{
         endpoint: host+"/oauth2/simple",
         "method":'POST',
         headers:{'Content-Type': 'application/x-www-form-urlencoded'},
     },
     "apps":{
      endpoint: host+"/eureka/apps",
      "method":'GET',
      headers:{'Accept': 'application/json'}
     }
   }
 );
 
 var apis=function(name,params=null){
   if(name==null){
     window.console.error('service name can\'t empty');
     throw new Error('miss required parameter \'service name\'');
   }
   var serviceConfig=services[name];
   if(!serviceConfig){
     window.console.error('did you config service named ',name);
     throw new Error('can\'t find service configurations of '+ name);
   }
   //apply params of this service
   var request=applyParams(params,Object.assign({},serviceConfig));
   if(typeof request.endpoint==='function'){
     request.endpoint=request.endpoint(params);
   }

   //apply authentication token in headers if service config don't claim auth header.
   if(serviceConfig.emitAuth){
    let authentication=store.get('authentication');
    if(authentication!=null && authentication.authenticated){
      if(request.headers){
        request.headers['Authorization']='Bearer '+authentication.access_token;
      }
    }else{
      window.console.warn("it seems authentication is unpresent,the auth header can't set"); 
    }
   }
   
  
  
 
    return fetch(request.endpoint,request).then(function(response){
      if(response.ok){
       var contentType = response.headers.get('content-type');
       if(contentType && contentType.includes('application/json')) {
         return response.json();
       }else if(contentType && contentType.includes('image/jpeg')){
         return response.blob();
       }else{
         return response;
       }
      }
      throw new Error('Network response was not ok.'+response.statusText) ;
      //Promise.reject({'error':'Network response was not ok.'});
    },function(e){
       window.console.log('There has been a problem with your fetch operation: ', e.message);
       throw new Error(e.message+' encountered a issue when call api '+request.endpoint);
    })
 }
 
 
 var applyParams=(param,request)=>{
   //apply params into request body
     if(param && request.method!='GET'){
         if(param&&request.headers&&request.headers['Content-Type']){
             if(request.headers['Content-Type'].indexOf('json')!=-1){
               request.body=JSON.stringify(param);
             }else if(request.headers['Content-Type'].indexOf('x-www-form-urlencoded')!=-1){
               var keys=Object.keys(param);
               var usr=new URLSearchParams();
               for(var i=0; i<keys.length;i++){
                 var key=keys[i];
                 usr.append(key,param[key]);
               }
               request.body=usr.toString();
             }
         }
     }
 
   //custom callback function
     return request;
 }
 
 export default apis;