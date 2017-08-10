import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import * as ethereumActions from '../actions/EthereumActions';


class EthereumInfo extends Component {

    constructor(props, context) {
        super(props, context);

        // Check authorization
        const { getEthereumInfo } = this.props.actions;
        getEthereumInfo();
    }

    render() {
        const { info } = this.props;

        return (

            <div>
                SKINCOIN address: { info.skincoin_address == '0x2bdc0d42996017fce214b21607a515da41a9e0c5' ?
                <span style={{color:'green'}}>{info.skincoin_address}</span> : <span style={{color:'red'}}>{info.skincoin_address}</span>}
                <div className='text-right pull-right'>{ info.network == 'MAIN_NET' ?
                    <span style={{color:'green'}}>{info.network}</span> : <span style={{color:'red'}}>{info.network}</span>}
                </div>
            </div>

        )
    }
}


const mapStateProps = (state) => {
    return {
        info: state.ethereum.info
    };
};

const mapDispatchProps = (dispatch) => {
    return {
        actions: bindActionCreators(ethereumActions, dispatch)
    };
};

export default connect(mapStateProps, mapDispatchProps)(EthereumInfo);