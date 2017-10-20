const moment = require('moment')

const getRandomInt = (min, max) => {
  min = Math.ceil(min)
  max = Math.floor(max)
  return Math.floor(Math.random() * (max - min)) + min
}

const fourHoursInSeconds = 4 * 60 * 60

const subtractRandomSeconds = date => date.subtract(getRandomInt(1, fourHoursInSeconds), 'seconds')

const semiRandomTimestamp = () => subtractRandomSeconds(moment())

const generateTimestampToday = () => semiRandomTimestamp().unix()

const generateTimestampYesterday = () => semiRandomTimestamp().subtract(1, 'day').unix()

const generateTimestampTwoDaysAgo = () => semiRandomTimestamp().subtract(2, 'day').unix()

const generateTimestampLastWeek = () => semiRandomTimestamp().subtract(7, 'day').unix()

const generateTimestampTwoWeeksAgo = () => semiRandomTimestamp().subtract(14, 'day').unix()

module.exports = function() {
  const data = [
    {
      utcTimestamp: generateTimestampToday(),
      identity: 'Suspected Spam',
      reputation: 'flagged',
      id: '77983f5f-c149-44b9-8b8a-483534a4a553',
      termination: 'NoAnswer',
      callDirection: 'Incoming',
      phone: '+19991113333',
    }, {
      utcTimestamp: generateTimestampToday(),
      identity: 'Karen Ortiz',
      id: 'd2032b28-5ce0-40a0-90df-066a8d686c0d',
      termination: 'Disconnected',
      callDirection: 'Incoming',
      phone: '+19991113333',
    }, {
      utcTimestamp: generateTimestampYesterday(),
      identity: 'Suspected Spam',
      reputation: 'flagged',
      id: '5749e364-b4c2-44b6-8f78-f1fcb8da9af0',
      termination: 'Disconnected',
      callDirection: 'Incoming',
      phone: '+19998887005',
    }, {
      utcTimestamp: generateTimestampYesterday(),
      identity: 'Suspected Spam',
      reputation: 'flagged',
      id: '7f4e16f7-f06e-4be5-a721-364807f780fe',
      termination: 'Disconnected',
      callDirection: 'Incoming',
      phone: '+19998887005',
    }, {
      utcTimestamp: generateTimestampTwoDaysAgo(),
      identity: 'Suspected Spam',
      reputation: 'flagged',
      id: '41eddbf0-b265-4cad-8449-27d2736e74cd',
      termination: 'Disconnected',
      callDirection: 'Incoming',
      phone: '+19998887013',
    }, {
      utcTimestamp: generateTimestampTwoDaysAgo(),
      identity: 'Potential Fruad',
      reputation: 'blocked',
      id: '0e9cd128-7e78-4bb0-8395-f9c93c007e5e',
      termination: 'AutoBlocked',
      callDirection: 'Incoming',
      phone: '+19998887020',
    }, {
      utcTimestamp: generateTimestampLastWeek(),
      identity: 'Political Call',
      reputation: 'flagged',
      id: '0e9cd128-7e78-4bb0-8395-f9c93c007e5e',
      termination: 'Disconnected',
      callDirection: 'Incoming',
      phone: '+19998887020',
    }, {
      utcTimestamp: generateTimestampLastWeek(),
      id: '1473dfde-0426-4d04-ba51-d7c3a7de0f25',
      termination: 'Disconnected',
      callDirection: 'Incoming',
      phone: '+14849590008',
    }, {
      utcTimestamp: generateTimestampTwoWeeksAgo(),
      id: '9269651f-96a0-4898-9783-a792e4261fb1',
      termination: 'Busy',
      callDirection: 'Outgoing',
      phone: '+19991113333',
    }, {
      utcTimestamp: generateTimestampTwoWeeksAgo(),
      id: 'fe7bd6a9-d9ee-41f9-87af-da3747253bbd',
      termination: 'Disconnected',
      callDirection: 'Incoming',
      phone: '+19991113333',
    }, {
      utcTimestamp: generateTimestampTwoWeeksAgo(),
      identity: 'Suspected Spam',
      id: '1848ae79-52bd-4d9c-b3bd-001d9dc408f9',
      termination: 'Disconnected',
      callDirection: 'Incoming',
      phone: '+19991113333',
    },
  ]

  return data.sort((a, b) => b.utcTimestamp - a.utcTimestamp)
}
