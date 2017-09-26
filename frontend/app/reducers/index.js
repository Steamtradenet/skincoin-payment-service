import {combineReducers} from 'redux';
import loginReducer from './loginReducer';
import tableReducer from './tableReducer';
import ethereumReducer from './ethereumReducer';



export default combineReducers({
    session: loginReducer,
    table: tableReducer,
    ethereum: ethereumReducer
});
