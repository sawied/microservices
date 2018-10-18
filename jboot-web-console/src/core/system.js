import React from "react"
import { createStore, applyMiddleware, bindActionCreators, compose } from "redux"
import Im, { fromJS, Map } from "immutable"
import promiseMiddleware from 'redux-promise';

import deepExtend from "deep-extend"
import rootReducer,{ge} from './reducer'
import {isPromise} from './utils';


export default class Store {
    constructor(opts = {}) {
        deepExtend(this, {
            state: {
            },//default state of redux 
        }, opts);

        this.store = configureStore(rootReducer, fromJS(this.state))
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
            store.dispatch(ge(error.message));
        });
    };
  }



function configureStore(rootReducer, initialState) {

    let middlwares = [
        globalErrorMiddleware(),
        promiseMiddleware
    ]

    const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose

    return createStore(rootReducer, initialState, composeEnhancers(
        applyMiddleware(...middlwares)
    ))

}