import React from 'react';
import {close,init} from './faceplate/actions';
import {dsActionCreators} from './actions';
import {FormattedRelative} from 'react-intl';
import _ from 'lodash';


class AppViewer extends React.Component{


    constructor(props){
        super(props)
        this.detailListner = this.props.detailListner;
    }


    displayInstanceHealth =(e,instanceId) =>{
        e.preventDefault();
        this.detailListner(instanceId);
    }


    render(){
        let {data} = this.props;

        let instances = data.instance.map((inst,index)=>{

            let instanceStatusClass= "badge badge-" + (inst.status =='UP'? "success":"warning");

            return (<div key={index} className="instance-detail">
                <div className="instance-status"><span className={instanceStatusClass}>{inst.status}</span></div>
                <p className="h5 instance-title">
                <a className="" href="#" onClick={(e)=>this.displayInstanceHealth(e,inst.instanceId)}>
                {inst.instanceId}
                </a>
                </p>
                <div className="instance-item-property">
                       <ul>
                        <li className="item-property"><label>Host Name:</label><span>{inst.hostName}</span></li>
                        <li className="item-property"><label>Ip Address:</label><span>{inst.ipAddr}</span></li>
                        <li className="item-property"><label>last update time:</label><span><FormattedRelative value={new Date(_.toNumber(inst.lastUpdatedTimestamp))}/></span></li>
                       </ul>
                </div>
            </div>)
        })

        return (<div className="col-sm-6">
        <div className="card application-container shadow">
         <div className="card-header">
          {data.name}
         </div>
         <div className="card-body">
        
            {instances}
         
         </div>
         </div>
        </div>)
    }
}

export default AppViewer;
