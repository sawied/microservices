import {intlReducer} from '../core/intlProvider'
import { combineReducers } from "redux-immutable"

export default combineReducers({
    intl:intlReducer,
})