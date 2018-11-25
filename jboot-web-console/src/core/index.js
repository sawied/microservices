import React from 'react';
import ReactDOM from 'react-dom';
import Root from "../containers";
import API from './apis';
import store from 'store';


async function launch(){
    let result = null;
    
    try{
        result = await API('info',{});
    }catch(e){
        window.console.error("it seems not logon?",e);
        store.remove("authentication");
    }
   
    ReactDOM.render(
        <Root/>,document.getElementById('web-console-ui'));
}

launch();


