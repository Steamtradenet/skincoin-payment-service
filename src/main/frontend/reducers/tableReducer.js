import {
    GET_APPS_REQUEST, GET_APPS_SUCCESS, GET_APPS_FAILURE,
    ADD_APP_REQUEST, ADD_APP_SUCCESS, ADD_APP_FAILURE,
    UPDATE_APP_REQUEST, UPDATE_APP_SUCCESS, UPDATE_APP_FAILURE,
    } from '../actions/TableActions';

export const initialState = {
    isFetching: false,
    apps: []
};

const reducer = (state = initialState, action) => {
    switch (action.type) {
        case GET_APPS_REQUEST:
            return Object.assign({}, state, {
                isFetching: true,
                errorMessage: '',
                apps: []
            });
        case GET_APPS_SUCCESS:
            return Object.assign({}, state, {
                isFetching: false,
                errorMessage: '',
                apps: action.payload
            });
        case GET_APPS_FAILURE:
            return Object.assign({}, state, {
                isFetching: false,
                errorMessage: action.error,
                apps: []
            });

        case ADD_APP_REQUEST:
            return Object.assign({}, state, {
                isFetching: true,
                errorMessage: ''
            });
        case ADD_APP_SUCCESS:
            return Object.assign({}, state, {
                isFetching: false,
                errorMessage: '',
                apps: [...state.apps, action.payload]
            });
        case ADD_APP_FAILURE:
            return Object.assign({}, state, {
                isFetching: true,
                errorMessage: ''
            });

        case UPDATE_APP_REQUEST:
            return Object.assign({}, state, {
                isFetching: true,
                errorMessage: ''
            });
        case UPDATE_APP_SUCCESS:
            return Object.assign({}, state, {
                isFetching: false,
                errorMessage: '',
                apps: state.apps.map(app => app.id === action.payload.id ? action.payload: app )
            });
        case UPDATE_APP_FAILURE:
            return Object.assign({}, state, {
                isFetching: true,
                errorMessage: ''
            });
      default:
            return state
    }
};

export default reducer;



