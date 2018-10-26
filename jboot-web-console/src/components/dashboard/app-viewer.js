import React from 'react';


class AppViewer extends React.Component{
    render(){
        let {data} = this.props;

        let instances = data.instance.map((inst,index)=>{
            return (<div key={index}>
                <h5>{inst.instanceId}</h5>
                <div>
                    {inst.hostName}
                    {inst.ipAddr}
                </div>
            </div>)
        })

        return (<div className="col-sm-6">
        <div className="card application-container">
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
