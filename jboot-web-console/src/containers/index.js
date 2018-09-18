import React from 'react';
import {Provider} from 'react-redux';
import {
    BrowserRouter,
    Route,
    Switch
  } from 'react-router-dom';

import System from '../core/system';
import Navigatior from '../components/navigator';
import Dashboard from '../components/dashboard';
import logger from '../components/logger';

let system=new System();
export default () => (
    <Provider store={system.getStore()}>
        <BrowserRouter>
                <>
                <div className="navigation">
                 <Navigatior/>
                </div>
                <div className="app-context-body">
                    <Switch>
                        <Route exact path='/' component={Dashboard} />
                        <Route path="/logger" component={logger} />
                        <Route component={NoMatch} />
                    </Switch>
                </div>
                </>
        </BrowserRouter>
    </Provider>
)

const NoMatch = ({ location }) => (
    <div>
      <h3>
        No match for <code>{location.pathname}</code>
      </h3>
    </div>
  );
