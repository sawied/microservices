import React from 'react';
import { FormattedMessage } from 'react-intl'
import Menu from './menu';
export default ()=>(<>
     <div className="list-group-container">
     <Menu></Menu>
     </div>
    <div className="content-body">
    
   <FormattedMessage id="greeting"  defaultMessage="你好!" /> 
    
    </div>
    </>
)