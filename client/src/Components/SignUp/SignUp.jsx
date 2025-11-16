import React, { useState } from 'react';
import { FaUser, FaLock } from "react-icons/fa";
import { MdEmail } from 'react-icons/md';

export const SignUp = ({onSubmit, loading}) => {
  const [formData, setFormData] = useState({ firstName: '', lastName: '', email: '', plainPassword: '', username: '' })

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((prev) => ( { ...prev, [name]: value }));
  }

  const handleSubmit = async(event) => {
    event.preventDefault();
    if (onSubmit) {
      await onSubmit(formData);
    }
  }

  return (
    <form className="form" onSubmit={handleSubmit} noValidate>
      <div className="input">
        <MdEmail />
        <input 
          id="first-name"
          name='firstName'
          type="text"
          placeholder="first name"
          value={formData.firstName}
          onChange={handleChange}
        />
      </div>
      <div className="input">
        <MdEmail />
        <input 
          id="last-name"
          name='lastName'
          type="text"
          placeholder="last name"
          value={formData.lastName}
          onChange={handleChange}
        />
      </div>
      <div className="input">
        <MdEmail />
        <input 
          id="email"
          name='email'
          type="email"
          placeholder="email"
          value={formData.email}
          onChange={handleChange}
        />
      </div>
      <div className="input">
        <FaLock />
        <input 
          id="plain-password"
          name='plainPassword'
          type="password"
          placeholder="password"
          value={formData.plainPassword}
          onChange={handleChange}
        />
      </div>
      <div className="input">
        <FaUser />
        <input 
          id="username"
          name='username'
          type="text"
          placeholder="username"
          value={formData.username}
          onChange={handleChange}
        />
      </div>
      <div className='submit-container'>
        <button type='submit'className="submit" disabled={loading}>Sign Up</button>
      </div>
    </form>
  )
}
