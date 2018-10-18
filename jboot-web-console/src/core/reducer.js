import {intlReducer} from '../core/intlProvider'
import { combineReducers } from "redux-immutable"
import { createAction, handleAction } from 'redux-actions';
import Immutable from 'immutable';
import {authReducer} from '../components/auth/actions';

export const ge = createAction('GLOBAL_ERROR');

const defaultState=Immutable.fromJS({
    error:null
})

const errorReducer = handleAction(ge,(state,action)=>{
    return state.set('error',action.payload);
},defaultState);

const rootReducers = combineReducers({
    intl:intlReducer,
    ge:errorReducer,
    auth:authReducer
})

export default rootReducers;
