import { createActions, handleActions} from 'redux-actions';
import {fromJS} from 'immutable';
import { combineReducers } from "redux-immutable"
import apis from '../../core/apis';
import { isArray } from 'util';
import  facePlateReducer from './faceplate/actions';

const defaultState=fromJS({
    applicationCount:0,
    applications:[]
});

export const dsActionCreators = createActions({
    getApps: async ()=>{
       const  result = await apis('apps',{});
      return result;
      },
    getInstanceDetails : async (instanceId)=>{
        const result = await apis('instance-details',{instanceId})
    }
      }); 

 const bootReducer = handleActions({
    getApps:{
   next(state,action){
     window.console.log("get app data from server :",action.payload);
     if(action.payload.applications&&Array.isArray(action.payload.applications.application)){
         let appstatus=state.setIn(['applicationCount'],action.payload.applications.application.length);
        return appstatus.setIn(['applications'],action.payload.applications.application);
     }
      return state;
   },
   throw(state,action){
     state.setIn(["applicationCount"],0);
     //state.setIn(["applications"],[]);
     return state;
   }
 }
},
defaultState
);


export const dsReducer = combineReducers({ds:bootReducer,faceplate:facePlateReducer});


