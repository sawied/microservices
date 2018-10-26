import React from 'react';
import ReactDOM from 'react-dom';
import Root from "../containers";
import {auth} from './system';

if(!auth().authenticated){
    window.location.href="logon";
}
ReactDOM.render(
    <Root/>,document.getElementById('web-console-ui'));