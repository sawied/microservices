import React from 'react';
import { connect } from 'react-redux';
class Error extends React.Component{
    
    constructor(props){
        super(props);
      }

    render(){
        let {error} = this.props;
        if(error){
            return (<div className="error-panel">{error}</div>)
        }else{
            return null;
        }
    }
    
}

const mapStateToProps = (state) => {
    return {
        error: state.getIn(['ge','error'])
    }
}
export default connect(mapStateToProps)(Error);