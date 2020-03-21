import React from 'react';
import ReactDOM from 'react-dom';
import Root from "../containers";
import API from './apis';
import store from 'store';



API('info',{}).then(()=>{
    window.console.info("look like ,token before is availble.");
}).catch((e)=>{
    window.console.error("it seems not logon?",e);
    store.remove("authentication");
}).finally(()=>{
    window.console.info("launch main app");
    ReactDOM.render(
        <Root/>,document.getElementById('web-console-ui'));
});


