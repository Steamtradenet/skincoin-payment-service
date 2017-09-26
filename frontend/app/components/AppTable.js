import React, { Component } from 'react';
import { BootstrapTable, TableHeaderColumn } from 'react-bootstrap-table';

import PropTypes from 'prop-types';

import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import * as tableActions from '../actions/TableActions';

function addressValidator(value) {
    if (!value) {
        return 'Payout address is required!';
    } else if (!/^0x[0-9a-fA-F]{40}$/.test(value)) {
        return 'Incorrect payout address. Must be 20 bytes and start from"0x" ';
    }
    return true;
}

function appIddValidator(value) {
    if (!value) {
        return 'App ID is required!';
    }
    return true;
}

function apiKeyValidator(value) {
    if (!value) {
        return 'API Key is required!';
    }
    return true;
}

function authSecretValidator(value) {
    if (!value) {
        return 'Auth secret is required!';
    }
    return true;
}

function nameValidator(value) {
    if (!value) {
        return 'Name is required!';
    }
    return true;
}

function passwordValidator(value) {
    if (!value) {
        return 'Payout password is required!';
    }
    return true;
}


class AppTable extends Component {

    constructor(props, context) {
        super(props, context);

        const { getApps } = this.props.actions;
        getApps();

        this.onAddRow = this.onAddRow.bind(this);
        this.onUpdateRow = this.onUpdateRow.bind(this);
    }

    onAddRow(data) {
        const { addApp } = this.props.actions;
        addApp(data);
    }

    onUpdateRow(row, cellName, cellValue) {
        const { updateApp } = this.props.actions;
        if (row[cellName] != cellValue) {
            let newObj = {};
            newObj[cellName] = cellValue;

            updateApp(Object.assign({}, row, newObj));
        }
    }

    render() {
        const options = {
            noDataText: 'No registered apps found. To register a new app press "New" button',
            onAddRow: this.onAddRow
        };

        const cellEditProp = {
            mode: 'click',
            blurToSave: true,
            beforeSaveCell: this.onUpdateRow
        };

        return (
            <BootstrapTable data={ this.props.apps } cellEdit={ cellEditProp }
                            insertRow={ true }
                            options={ options }>
                <TableHeaderColumn width='5%' dataField='id' editable={ { type: 'number', validator: appIddValidator} } isKey={ true }>App ID</TableHeaderColumn>
                <TableHeaderColumn width='15%' dataField='name' editable={ { type: 'text', validator: nameValidator} }>Name</TableHeaderColumn>
                <TableHeaderColumn width='15%' dataField='token' editable={ { type: 'text', validator: apiKeyValidator} }>API Key</TableHeaderColumn>
                <TableHeaderColumn width='20%' dataField='from_address' editable={ { type: 'text', validator: addressValidator} }>Payout address</TableHeaderColumn>
                <TableHeaderColumn width='15%' dataField='from_password' editable={ { type: 'text', validator: passwordValidator} }>Payout password</TableHeaderColumn>
                <TableHeaderColumn width='20%' dataField='callback_url'>Callback URL</TableHeaderColumn>
                <TableHeaderColumn width='15%' dataField='auth_secret' editable={ { type: 'text', validator: authSecretValidator} }>Auth secret</TableHeaderColumn>
                <TableHeaderColumn width='10%' dataField='enable_callback' editable={ { type: 'checkbox', options: { values: 'true:false' } } }>Enable callback</TableHeaderColumn>
            </BootstrapTable>
        );
    }
}

AppTable.propTypes = {
    actions: PropTypes.object.isRequired
};

const mapStateProps = (state) => {
    return {
        apps: state.table.apps
    }
};

const mapDispatchProps = (dispatch) => {
    return {
        actions: bindActionCreators(tableActions, dispatch)
    };
};

export default connect(mapStateProps, mapDispatchProps)(AppTable);