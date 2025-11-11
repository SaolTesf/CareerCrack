import React from 'react';
import { FaUser, FaLock } from "react-icons/fa";

export const Login = ({onSubmit, loading}) => {

  const [formData, setFormData] = useState({ identifier: '', password: '' });

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  }

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (onSubmit) {
      await onSubmit(formData);
    }
  }

  return (
    <form className="form" onSubmit={handleSubmit} noValidate>
      <div className="input">
        <FaUser />
        <input 
          id="username-email"
          type="username/email"
          placeholder="username/email"
          value={formData.identifier}
          onChange={handleChange}
        />
      </div>
      <div className="input">
        <FaLock />
        <input 
          id="password"
          type="password"
          placeholder="password"
          value={formData.password}
          onChange={handleChange}
        />
      </div>
      <div className="forgot-password">
          Forgot Password? <span>Click Here!</span>
      </div>
      <div className='submit-container'>
        <button type='submit'className="submit">Log In</button>
      </div>
    </form>
  )
}
