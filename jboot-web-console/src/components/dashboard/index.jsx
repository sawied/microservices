import React from 'react';
import { connect } from 'react-redux';
import Menu from './app-menu';
import {dsActionCreators} from './actions';
import Apps from './apps';
import Faceboard from './faceplate';
import {init} from './faceplate/actions'


class Applist extends React.Component{

    constructor(props){
        super(props);
        this.getApps = this.props.getApps;
        
      }

    componentDidMount(){
        this.getApps();
      }

    render(){
        let {applicationCountNumber,applications,faceplate,instanceDetailListner} = this.props;
        return (<>
            <div className="list-group-container">
            <Menu applicationCountNumber={applicationCountNumber}></Menu>
            </div>
           <div className="content-body">
             <Apps applications={applications} detailListner={instanceDetailListner}/>
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
const mapDispatchToProps = (dispatch)=>{
     return {
       instanceDetailListner: (instanceId)=>{
         var result=dispatch(dsActionCreators.getInstanceDetails(instanceId)
         //.then((data)=>dispatch(init({type:'instanceDetail',data})))
         ).then(
           (data)=>{
            if(data&&data.payload){
              dispatch(init({type:'instanceDetail',data:data.payload}))
            }
            }
         );
         // window.console.log(result);

       },
       getApps:()=>{
         dispatch(dsActionCreators.getApps());
       }
    }
}


export default connect(mapStateToProps,mapDispatchToProps)(Applist);