import React from 'react';
import PropTypes from 'prop-types';
import Octicon, {X} from '@githubprimer/octicons-react';
import {FormattedMessage} from 'react-intl';
import InstanceDetail from './instance-details';



/**
 * 
 * data construct of this component
 * 
 * {
 * open：boolean  //visiable or disvisiable
 * type：string   // compenoent type
 * data：object   //data for render
 * }
 * 
 */
class Faceboard extends React.Component{
    
    constructor(props){
        super(props);
        this.state={open:false}
        this.closeFaceboard=this.closeFaceboard.bind(this);
      }

      closeFaceboard(){
          this.setState((state,props)=>{
              return {open:false};
          });
      }

      componentWillReceiveProps(nextProps){
          if(nextProps.type&&nextProps.data){
            this.setState({open:true});
          }
      }

      render(){
         
          if(!this.state.open){
              return null;
          }

          let {type,data,intl} = this.props;
          let component = (<div>No component be matched! </div>);
          if(type=="instanceDetail"){
            component = (<InstanceDetail {...data} />);
          }


          return (
          <div className="face-board-container">
          <div className="face-board shadow">
          <div className="faceboard-title">
           <span>
           <FormattedMessage id={type} defaultMessage="title" />
           </span>
           <button type="button" className="close" aria-label="Close" onClick={this.closeFaceboard}>
           <Octicon icon={X} size='small' ariaLabel='close' verticalAlign='top'></Octicon>
           </button>
          </div>
          <div className="card-body">
           {component}
          </div>
          </div>
          </div>
          )
      }


    }
    Faceboard.propTypes = {
        child:PropTypes.element
      }

export default Faceboard;