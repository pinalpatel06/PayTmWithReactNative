/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  View,
  TouchableWithoutFeedback,
  TextInput
} from 'react-native';
import { Button } from 'native-base'

import PayTm from './PatTm';



const instructions = Platform.select({
  ios: 'Press Cmd+R to reload,\n' +
    'Cmd+D or shake for dev menu',
  android: 'Double tap R on your keyboard to reload,\n' +
    'Shake or press menu button for dev menu',
});

export default class App extends Component {

  startPayTmPage(orderId, checksum) {
    PayTm.startPayment(orderId, checksum)
      .then((bundle) => {
        console.log("Response: ", bundle)
      })
      .catch((error) => {
        console.log("error", error)
      })
  }

  generateCheckSum() {
    let orderId = "knxp27"
    fetch(
      'http://globalcity-20.appspot.com/api/v1/checksum/generate', {
        method: 'POST',
        headers: {
          Accept: 'application/json',
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          orderId
        })
      }
    ).then((response) => {
      console.log("checkSum: ", response._bodyText)

      let checksumData = JSON.parse(response._bodyText)
      console.log("Object ", checksumData.checksum)

      this.startPayTmPage(orderId, checksumData.checksum)
    })
      .catch((error) => {
        console.log("error", error)
      })
  }

  render() {
    return (
      <View>
        <View style={styles.container}>
          <Text style={styles.welcome}>
            Welcome to React Native!
        </Text>
          <Text style={styles.instructions}>
            To get started, edit App.js
        </Text>
          <Text style={styles.instructions}>
            {instructions}
          </Text>
        </View>
        <View style={{ justifyContent: 'center', alignItems: 'center', marginTop: 20 }}>
          <View>
            <Button style={{padding: 10}} onPress = {() => this.generateCheckSum()}>
              <Text>Pay Now</Text>
            </Button>
          </View>
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});
