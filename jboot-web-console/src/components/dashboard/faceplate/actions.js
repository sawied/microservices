import { createAction,handleActions } from 'redux-actions';
import {Map} from 'immutable';

let initStatus = Map({"open":false,"type":null,"data":null});

 const CLOSE_FACEPLATE="faceplate/close";

 const LOAD_FACEPLATE="faceplate/load";

export const close = createAction(CLOSE_FACEPLATE);

export const init = createAction(LOAD_FACEPLATE);

export const facePlateReducer=handleActions({
  [CLOSE_FACEPLATE]:(state)=>{
    return state.setIn(['open'],false)
  },
  [LOAD_FACEPLATE]:(state,action)=>{
    window.console.log("state action");
    return state.setIn(['open'],true).setIn(['data'],action.payload.data).setIn(['type'],action.payload.type);
}
},initStatus);

