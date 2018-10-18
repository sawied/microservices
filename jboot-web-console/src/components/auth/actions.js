import { createActions, handleActions, combineActions } from 'redux-actions';
import {Map} from 'immutable';
import apis from '../../core/apis';

const defaultState=Map({authenticated:false,user_name:'unknow',authorities:[],email:null,jti:null});

export const actionCreators = createActions({
    auth: async (username,password)=>{
       const  result = await apis('auth',{username,password});
      return result;
      }
      }); 

export const authReducer=handleActions({
 auth:{
   next(state,action){
     window.console.log("authentication :",action.payload);
      const auth =state.merge(Map(Object.assign({authenticated:true},action.payload)));
      return auth;
   },
   throw(state,action){
     state.setIn(["authenticated"],false);
     return state;
   }
 }
},
defaultState
);
