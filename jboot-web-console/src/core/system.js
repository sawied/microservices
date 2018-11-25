import { createStore, applyMiddleware, bindActionCreators, compose } from "redux";
import Im, { fromJS, Map } from "immutable";
import promiseMiddleware from 'redux-promise';
import logger from 'redux-logger';

import deepExtend from "deep-extend";
import rootReducers,{ge} from './reducer';
import {isPromise} from './utils';
import createBrowserHistory from "history/createBrowserHistory";
import {getAuth} from './utils';


export  class StoreClass {
    constructor(opts = {}) {
        deepExtend(this, {
            state: {
            },//default state of redux 
        }, opts);

        this.store = configureStore(rootReducers, fromJS(this.state))
    }

    getStore() {
        return this.store
    }

}

function globalErrorMiddleware() {
    return store => next => action => {
  
      // If not a promise, continue on
      if (!isPromise(action.payload)) {
        return next(action);
      }
        return next(action).catch(error => {
            window.console.warn(`${action.type} caught at middleware with reason: ${JSON.stringify(error.message)}.`);
            store.dispatch(ge(error.message+new Date().getTime().toString()));
        });
    };
  }



function configureStore(rootReducer, initialState) {

    let middlwares = [
        globalErrorMiddleware(),
        promiseMiddleware,
        logger
    ]

    const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose

    return createStore(rootReducer, initialState, composeEnhancers(
        applyMiddleware(...middlwares)
    ))

}



const createHistory = ()=>{

    let history=createBrowserHistory({basename: "", forceRefresh: false});
    if(!getAuth().authenticated){
        history.push("/logon",{});
    }
    window.console.log("create history ......");
    return history;

}

export const history = createHistory();

