import React, { Component } from "react";
import { getSystemSetup, saveSystemSetup } from "../../../store/actions/system";
import connect from "react-redux/es/connect/connect";
import SettingsConfigurationForm from "./SettingsConfigurationForm";
import PropTypes from "prop-types";

class SettingsConfiguration extends Component {
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
        return (
            <div>
                <h1>
                    <span className="fa fa-cog" />
                    Settings
                </h1>
                <SettingsConfigurationForm
                    fetchingSetupStatus={this.props.fetchingSetupStatus}
                    updateStatus={this.props.updateStatus}
                    settingsData={this.props.currentSettingsData}
                    fieldErrors={this.props.fieldErrors}
                    getSettings={this.getSettings}
                    saveSettings={this.saveSettings}
                />
            </div>
        );
    }
}

SettingsConfiguration.propTypes = {
    fetchingSetupStatus: PropTypes.string.isRequired,
    getSettings: PropTypes.func.isRequired,
    saveSettings: PropTypes.func.isRequired,
    updateStatus: PropTypes.string,
    currentSettingsData: PropTypes.object,
    fieldErrors: PropTypes.object
};

SettingsConfiguration.defaultProps = {
    fieldErrors: {},
    currentSettingsData: {},
    updateStatus: ''
};

const mapStateToProps = state => ({
    fetchingSetupStatus: state.system.fetchingSetupStatus,
    updateStatus: state.system.updateStatus,
    currentSettingsData: state.system.settingsData,
    fieldErrors: state.system.error
});

const mapDispatchToProps = dispatch => ({
    getSettings: () => dispatch(getSystemSetup()),
    saveSettings: setupData => dispatch(saveSystemSetup(setupData))
});

export default connect(mapStateToProps, mapDispatchToProps)(SettingsConfiguration);
