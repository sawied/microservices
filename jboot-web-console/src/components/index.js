import React from 'react';
import {Provider} from 'react-redux';
import {
    BrowserRouter,
    Route,
    Switch
  } from 'react-router-dom';

export default () => (
    <Provider store={store}>
        <BrowserRouter>
            <div className="container">
                <div className="navigation"></div>
                <div className="">
                    <Switch>
                        <Route exact path='/' component={Home} />
                        <Route path="/todoApp" component={TodoApp} />
                        <Route path='/imager' component={imager} />
                    </Switch>
                </div>
            </div>
        </BrowserRouter>
    </Provider>
)
