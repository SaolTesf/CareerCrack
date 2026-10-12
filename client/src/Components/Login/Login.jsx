import React, { useState } from 'react';
import { FaUser, FaLock } from "react-icons/fa";

const InputField = ({ icon, error, ...inputProps }) => (
  <div className={`input ${error ? 'has-error' : ''}`}>
    {icon}
    <input aria-invalid={!!error} {...inputProps} />
    {error && <span className="field-error" role="alert">{error}</span>}
  </div>
);

export const Login = ({ onSubmit, loading, fieldErrors = {} }) => {
  const [formData, setFormData] = useState({ identifier: '', password: '' });

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (onSubmit) {
      await onSubmit(formData);
    }
  };

  return (
    <form className="form" onSubmit={handleSubmit} noValidate>
      <InputField
        icon={<FaUser />}
        id="username-email"
        name="identifier"
        type="text"
        placeholder="username/email"
        value={formData.identifier}
        onChange={handleChange}
        error={fieldErrors?.identifier}
      />
      <InputField
        icon={<FaLock />}
        id="password"
        name="password"
        type="password"
        placeholder="password"
        value={formData.password}
        onChange={handleChange}
        error={fieldErrors?.password}
      />

      <div className="forgot-password">
        Forgot Password? <span>Click Here!</span>
      </div>

      <div className="submit-container">
        <button type="submit" className="submit" disabled={loading}>Log In</button>
      </div>
    </form>
  );
};