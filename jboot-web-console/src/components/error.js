import React from 'react';
import { connect } from 'react-redux';
import {ge} from '../core/reducer';
import classNames  from 'classnames';
class Error extends React.Component{
    
    constructor(props){
        super(props);
        this.state={"close":false};
        this.dispatch=this.props.dispatch;
        this.dismiss=this.dismiss.bind(this);
      }

    componentDidMount(){
        let {error} = this.props;
        if(error){
            this.setState({"close":false});
            setTimeout(this.dismiss,5000);
        }
    }
    componentWillReceiveProps(nextProps){
        let {error} = nextProps;
        if(error){
            this.setState({"close":false});
            setTimeout(this.dismiss,5000);
        }
    }

    dismiss(){
        this.setState({"close":true});
    }

    render(){
        let {error} = this.props;
        var panelClass=classNames({
            alert:true,
            "alert-warning":true,
            "message-panel":true,
            dismiss:this.state.close
        });
        if(error){
            return (<div className={panelClass} onAnimationEnd={this.dismiss}>
            <strong className="alert-heading">warning !</strong>
            <br/>
             <p>{error}</p>
            </div>
            )
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