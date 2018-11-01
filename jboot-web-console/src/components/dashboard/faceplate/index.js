import React from 'react';
import PropTypes from 'prop-types';
import Octicon, {X} from '@githubprimer/octicons-react'
class Faceboard extends React.Component{
    
    constructor(props){
        super(props);
        this.state={open:true}
        this.closeFaceboard=this.closeFaceboard.bind(this);
      }

      closeFaceboard(){
          this.setState((state,props)=>{
              return {open:false};
          });
      }

      render(){
         
          if(!this.state.open){
              return null;
          }else

          return (
          <div className="face-board-container">
          <div className="face-board shadow">
          <div className="faceboard-title">
           <span>title</span>
           <button type="button" className="close" aria-label="Close" onClick={this.closeFaceboard}>
           <Octicon icon={X} size='small' ariaLabel='close' verticalAlign='top'></Octicon>
           </button>
          </div>
          <div className="card-body">
          <p>dispaly content</p>
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