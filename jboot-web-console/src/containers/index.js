import React from 'react';
import {Provider} from 'react-redux';
import {
    Router,
    Route,
    Switch
  } from 'react-router-dom';

import {StoreClass as System,history} from '../core/system';
import Dashboard from '../components/dashboard';
import logger from '../components/logger';
import App from '../components/app';
import OAuth from '../components/oauth';

let system=new System();

export default () => (
    <Provider store={system.getStore()}>
         <Router history={history}>
              <Switch>
              <Route path="/logon" component={OAuth}></Route>
              <App>
              <Route path="/" exact component={Dashboard}></Route>
              <Route path="/logger" component={logger}></Route>
              <Route component={NoMatch} />
              </App>
              </Switch>
          </Router>
    </Provider>
)

const NoMatch = ({ location }) => (
    <div>
      <h3>
        No match for <code>{location.pathname}</code>
      </h3>
    </div>
  );
