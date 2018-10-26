import React from 'react';
class AppMenu extends React.Component{
  constructor(props){
    super(props);
  }
  render(){
    let count = this.props.applicationCountNumber;
    return (<>
      <div className="application-summary-couter">
          <span className="badge badge-primary badge-pill applicaton-couter">{count}</span>
          <span className="applicaton-couter-title">Applications</span> 
      </div>
      <ul className="list-group">
      <li className="list-group-item active">Devices&Users</li>
      <li className="list-group-item">Device status</li>
      <li className="list-group-item">Morbi leo risus</li>
      <li className="list-group-item">Porta ac consectetur ac</li>
      <li className="list-group-item">Vestibulum at eros</li>
    </ul>
    </>
    )
  }
}

export default AppMenu;
