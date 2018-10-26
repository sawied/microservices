import React from 'react';
import {Provider} from 'react-redux';
import {
  BrowserRouter,
    Route,
    Switch
  } from 'react-router-dom';


import System from '../core/system';

import Dashboard from '../components/dashboard';
import logger from '../components/logger';
import IntlProvider from '../core/intlProvider';
import App from '../components/app';
import OAuth from '../components/oauth';

let system=new System();
export default () => (
    <Provider store={system.getStore()}>
       <IntlProvider>
         <BrowserRouter>
             <App>
              <Switch>
              <Route path="logon" component={OAuth}></Route>
              <Route path="/" exact component={Dashboard}></Route>
              <Route path="/logger" component={logger}></Route>
              <Route component={NoMatch} />
              </Switch>
             </App>  
          </BrowserRouter>
        </IntlProvider>
    </Provider>
)

const NoMatch = ({ location }) => (
    <div>
      <h3>
        No match for <code>{location.pathname}</code>
      </h3>
    </div>
  );
