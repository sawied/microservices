import React from 'react';
import { connect } from 'react-redux';
import Menu from './app-menu';
import {dsActionCreators} from './actions';
import Apps from './apps';
import Faceboard from './faceplate';


class Applist extends React.Component{

    constructor(props){
        super(props);
        this.dispatch = this.props.dispatch;
        
      }

    componentDidMount(){
        this.dispatch(dsActionCreators.getApps());
      }

    render(){
        let {applicationCountNumber,applications} = this.props;
        return (<>
            <div className="list-group-container">
            <Menu applicationCountNumber={applicationCountNumber}></Menu>
            </div>
           <div className="content-body">
             <Apps applications={applications} dispatch={this.dispatch}/>
           </div>
           <Faceboard/>
           </>
       )
    }
}

const mapStateToProps = state => ({
    applicationCountNumber: state.getIn(["apps",'applicationCount']),
    applications:state.getIn(['apps','applications'])
  })



export default connect(mapStateToProps)(Applist);