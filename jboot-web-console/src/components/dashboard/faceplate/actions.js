import { createAction,handleActions } from 'redux-actions';
import {Map} from 'immutable';

let initStatus = Map({"open":false,"type":null,"data":null});

export const CLOSE_FACEPLATE="faceplate/close";

export const LOAD_FACEPLATE="faceplate/load";

export const close = createAction(CLOSE_FACEPLATE);

export const init = createAction(LOAD_FACEPLATE);

export const facePlateReducer=handleActions({
  close:(state)=>{
    return state.setIn(['open'],false)
  },
  init:(state,action)=>{
    return state.setIn(['open'],true).set('data',action.payload.data).set('type',action.payload.type);
}
},initStatus);

