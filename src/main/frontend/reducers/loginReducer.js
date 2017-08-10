import {
    LOGIN_REQUEST, LOGIN_SUCCESS, LOGIN_FAILURE, LOGOUT_SUCCESS
} from '../actions/LoginActions';


export const initialState = {
    isFetching: false,
    authenticated: !!localStorage.getItem('jwtToken'),
    user: {}
};

const reducer = (state = initialState, action) => {
    switch (action.type) {
        case LOGIN_REQUEST:
            return Object.assign({}, state, {
                isFetching: true,
                authenticated: false,
                user: {}
            });
        case LOGIN_SUCCESS:
            return Object.assign({}, state, {
                isFetching: false,
                authenticated: true,
                errorMessage: '',
                user: action.payload ? action.payload.user: {}
            });
        case LOGIN_FAILURE:
            return Object.assign({}, state, {
                isFetching: false,
                authenticated: false,
                errorMessage: action.error,
                user: {}
            });
        case LOGOUT_SUCCESS:
            return Object.assign({}, state, {
                isFetching: false,
                authenticated: false,
                errorMessage: '',
                user: {}
            });
        default:
            return state
    }
};

export default reducer;



