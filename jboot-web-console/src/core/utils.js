



export function objMap(obj, fn) {
  return Object.keys(obj).reduce((newObj, key) => {
    newObj[key] = fn(obj[key], key)
    return newObj
  }, {})
}

export function objReduce(obj, fn) {
  return Object.keys(obj).reduce((newObj, key) => {
    let res = fn(obj[key], key)
    if(res && typeof res === "object")
      Object.assign(newObj, res)
    return newObj
  }, {})
}

// Redux middleware that exposes the system to async actions (like redux-thunk, but with out system instead of (dispatch, getState)
export function systemThunkMiddleware(getSystem) {
  return ({ dispatch, getState }) => { // eslint-disable-line no-unused-vars
    return next => action => {
      if (typeof action === "function") {
        return action(getSystem())
      }

      return next(action)
    }
  }
}

export function defaultStatusCode ( responses ) {
  let codes = responses.keySeq()
  return codes.contains(DEFAULT_RESPONSE_KEY) ? DEFAULT_RESPONSE_KEY : codes.filter( key => (key+"")[0] === "2").sort().first()
}



/**
 * Take an immutable map, and convert to a list.
 * Where the keys are merged with the value objects
 * @param {Immutable.Map} map, the map to convert
 * @param {String} key the key to use, when merging the `key`
 * @returns {Immutable.List}
 */
export function mapToList(map, keyNames="key", collectedKeys=Im.Map()) {
  if(!Im.Map.isMap(map) || !map.size) {
    return Im.List()
  }

  if(!Array.isArray(keyNames)) {
    keyNames = [ keyNames ]
  }

  if(keyNames.length < 1) {
    return map.merge(collectedKeys)
  }

  // I need to avoid `flatMap` from merging in the Maps, as well as the lists
  let list = Im.List()
  let keyName = keyNames[0]
  for(let entry of map.entries()) {
    let [key, val] = entry
    let nextList = mapToList(val, keyNames.slice(1), collectedKeys.set(keyName, key))
    if(Im.List.isList(nextList)) {
      list = list.concat(nextList)
    } else {
      list = list.push(nextList)
    }
  }

  return list
}
