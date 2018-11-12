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
        let {applicationCountNumber,applications,faceplate} = this.props;
        return (<>
            <div className="list-group-container">
            <Menu applicationCountNumber={applicationCountNumber}></Menu>
            </div>
           <div className="content-body">
             <Apps applications={applications} dispatch={this.dispatch}/>
           </div>
           <Faceboard {...faceplate}/>
           </>
       )
    }
}

const mapStateToProps = state => ({
    applicationCountNumber: state.getIn(["apps",'ds','applicationCount']),
    applications:state.getIn(['apps','ds','applications']),
    faceplate:{
      open: state.getIn(['apps','faceplate','open']),
      type: state.getIn(['apps','faceplate','type']),
      data: state.getIn(['apps','faceplate','data'])
    }
  })



export default connect(mapStateToProps)(Applist);