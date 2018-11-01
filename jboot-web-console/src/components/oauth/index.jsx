import { connect } from 'react-redux';
import LogonForm from './logon';
import {actionCreators} from './actions';

const mapStateToProps = state => ({
  authenticated: state.getIn(["auth","authenticated"])
})



const mapDispatchToProps = dispatch => ({
  doAuth: (username,password) => {
    var auth_action=actionCreators.auth(username,password);
    dispatch(auth_action);
  }
})

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(LogonForm);

