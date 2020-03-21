import React from 'react';
import AppViewer from './app-viewer';

class Apps extends React.Component{
    render(){
        let {applications,detailListner} = this.props;
        
        let appItmes = applications.map((app,index)=>(<AppViewer key={index} data={app} detailListner={detailListner}/>));

        return (<div className="contont-body">
        <h3>Applications list</h3>
        <div className="row">
          {appItmes}
        </div>
        </div>)
    }
}

export default Apps;
