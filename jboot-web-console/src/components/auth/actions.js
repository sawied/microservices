import { createActions, handleActions, combineActions } from 'redux-actions';

const defaultState={authenticated:false,username:'unknow',roles:[]};

const { auth_post } = createActions({
    AUTH_POST: (username,password) => ({username,password })
  });  

const reducer=handleActions(
    
);
