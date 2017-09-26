import React from 'react';
import PropTypes from 'prop-types';

const Input = ({ className, name, value, label, type, onChange, placeholder }) => (
    <div>
        {label && <label>{label}</label>}
        <div>
            <input {...{className, name, value, type, onChange }}/>
        </div>
    </div>
);

const { string, func } = PropTypes;

Input.propTypes = {
    name: string.isRequired,
    value: string.isRequired,
    label: string,
    type: string.isRequired,
    onChange: func.isRequired
};

export default Input;