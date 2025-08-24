import React from 'react';
import { FaUser, FaLock } from "react-icons/fa";

export const Login = () => {
  return (
    <div className="form">
      <div className="input">
        <FaUser />
        <input 
          id="username-email"
          type="username/email"
          placeholder="username/email"
        />
      </div>
      <div className="input">
        <FaLock />
        <input 
          id="password"
          type="password"
          placeholder="password"
        />
      </div>
    </div>
  )
}
