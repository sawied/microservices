import React from 'react';
import PropTypes from 'prop-types';
import _ from 'lodash'


class InstanceDetail extends React.Component{
    constructor(props){
        super(props);
    }

    render(){
        let {build,system} =this.props;

        let [buildAttributes,systemAttributes]=[
            ['name','group','artifact','version','time','project.description'],
            ['os_name','os_arch','os_version','processors','java_vendor','java_version','freeMemory','totalMemory','maxMemory']
            ];

        let buildInfo=this.buildInfomation(buildAttributes,build);

        let systemInfo=this.buildInfomation(systemAttributes,system);

        return (<div> 
            <p className="instance-detail-category">
                Build Information
            </p>
            <ul className="instance-attributes">
            {buildInfo}
            </ul>

            <p className="instance-detail-category">
                System Information
            </p>
            <ul className="instance-attributes">
              {systemInfo}
            </ul>
            

        </div>);
    }

    buildInfomation(attributies,root){
        return attributies.map((v,index)=>{
            return <li>
                <label>{v}:</label><span>{_.get(root,v)}</span>
            </li>
        });
    }
}

export default InstanceDetail;