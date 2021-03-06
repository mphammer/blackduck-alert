import connect from 'react-redux/es/connect/connect';
import React, { Component } from 'react';
import { getInitialSystemSetup, saveInitialSystemSetup } from 'store/actions/system';
import SettingsConfigurationForm from 'component/settings/SettingsConfigurationForm';
import PropTypes from 'prop-types';

class SetupPage extends Component {
    constructor(props) {
        super(props);
        this.getSettings = this.getSettings.bind(this);
        this.saveSettings = this.saveSettings.bind(this);
    }

    getSettings() {
        this.props.getSettings();
    }

    saveSettings(setupData) {
        this.props.saveSettings(setupData);
    }

    render() {
        const { errorMessage, actionMessage } = this.props;
        return (
            <div className="settingsWrapper">
                <div className="settingsContainer">
                    <div className="settingsBox">
                        <SettingsConfigurationForm
                            fetchingSetupStatus={this.props.fetchingSetupStatus}
                            updateStatus={this.props.updateStatus}
                            settingsData={this.props.currentSettingsData}
                            fieldErrors={this.props.fieldErrors}
                            getSettings={this.getSettings}
                            saveSettings={this.saveSettings}
                            errorMessage={errorMessage}
                            actionMessage={actionMessage}
                        />
                    </div>
                </div>
            </div>
        );
    }
}

SetupPage.propTypes = {
    fetchingSetupStatus: PropTypes.string.isRequired,
    getSettings: PropTypes.func.isRequired,
    saveSettings: PropTypes.func.isRequired,
    updateStatus: PropTypes.string,
    currentSettingsData: PropTypes.object,
    fieldErrors: PropTypes.object,
    errorMessage: PropTypes.string,
    actionMessage: PropTypes.string
};

SetupPage.defaultProps = {
    fieldErrors: {},
    currentSettingsData: {},
    updateStatus: '',
    errorMessage: null,
    actionMessage: null
};

const mapStateToProps = state => ({
    errorMessage: state.system.errorMessage,
    actionMessage: state.system.actionMessage,
    fetchingSetupStatus: state.system.fetchingSetupStatus,
    updateStatus: state.system.updateStatus,
    currentSettingsData: state.system.settingsData,
    fieldErrors: state.system.error
});

const mapDispatchToProps = dispatch => ({
    getSettings: () => dispatch(getInitialSystemSetup()),
    saveSettings: setupData => dispatch(saveInitialSystemSetup(setupData))
});

export default connect(mapStateToProps, mapDispatchToProps)(SetupPage);
