import { createActions,createAction,handleAction,handleActions, combineActions } from 'redux-actions';
import {Map} from 'immutable';
import apis from '../../core/apis';

let initStatus = {"open":false,"type":null,"data":null};


export const close = createAction('toggle');

export const closeReducer =handleAction(toggle,(state,action)=>{return state.setIn(['open'],false)});
export const dsActionCreators = createActions({
    toggle: ()=>{
        null
      }
    }); 