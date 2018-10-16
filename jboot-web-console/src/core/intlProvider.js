import { connect } from 'react-redux'
import { IntlProvider,addLocaleData } from 'react-intl'
import Im, { fromJS, Map } from "immutable"
import zhLocaleData from 'react-intl/locale-data/zh'
import enLocaleData from 'react-intl/locale-data/en'

import en from './en'
import zh from './zh'


addLocaleData([...zhLocaleData,...enLocaleData]);

function defaultSelector(state) {
  const intl = state.get('intl')
  return {
    key: intl.locale,
    locale:intl.locale,
    messages:intl.messages
  }
}

const mapStateToProps = (state, { intlSelector = defaultSelector }) =>
  intlSelector(state)

export default connect(mapStateToProps)(IntlProvider)





export const UPDATE = '@@intl/UPDATE'

const languages = {en,zh};

const initialState = {
    locale: navigator.language,
    messages:languages[navigator.language.split('-')[0]],
  };
  
  export function intlReducer(state = initialState, action) {
    if (action.type !== UPDATE) {
      return state
    }
  
    return { ...state, ...action.payload }
  }
