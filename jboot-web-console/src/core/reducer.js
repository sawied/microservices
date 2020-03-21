import { combineReducers } from "redux-immutable"
import { createAction, handleAction } from 'redux-actions';
import Immutable from 'immutable';
import {authReducer} from '../components/oauth/actions';
import {dsReducer} from '../components/dashboard/actions';

export const ge = createAction('GLOBAL_ERROR');

const defaultState=Immutable.fromJS({
    error:null
})

const errorReducer = handleAction(ge,(state,action)=>{
    return state.setIn(['error'],action.payload);
},defaultState);

const rootReducers = combineReducers({
   // intl:intlReducer,
    ge:errorReducer,
    auth:authReducer,
    apps:dsReducer
})

export default rootReducers;
