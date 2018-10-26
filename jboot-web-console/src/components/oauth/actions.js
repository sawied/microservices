import { createActions, handleActions, combineActions } from 'redux-actions';
import {Map} from 'immutable';
import apis from '../../core/apis';
import store from 'store';

const defaultState=Map({authenticated:false,user_name:'unknow',authorities:[],email:null,jti:null,access_token:null,expiration:null});

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
     let authentication=Object.assign({authenticated:true},action.payload);
     store.set("authentication",authentication);
      const auth =state.merge(Map(authentication));
      return auth;
   },
   throw(state,action){
     state.setIn(["authenticated"],false);
     return state;
   }
 }
},
initDefaultState()
);

function initDefaultState(){
  let auth=store.get('authentication');
  if(auth){
    return Map(auth);
  }else{
    return defaultState;
  }
}
