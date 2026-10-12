import React, { useState } from 'react';
import { FaUser, FaIdCard, FaAt, FaLock } from "react-icons/fa";
import { MdEmail } from "react-icons/md";

const InputField = ({ icon, error, ...inputProps }) => (
  <div className={`input ${error ? 'has-error' : ''}`}>
    {icon}
    <input aria-invalid={!!error} {...inputProps} />
    {error && <span className="field-error" role="alert">{error}</span>}
  </div>
);

export const SignUp = ({ onSubmit, loading, fieldErrors = {} }) => {
  const [formData, setFormData] = useState({
    firstName: '', lastName: '', email: '', plainPassword: '', username: ''
  });

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
        id="first-name"
        name="firstName"
        type="text"
        placeholder="first name"
        value={formData.firstName}
        onChange={handleChange}
        error={fieldErrors?.firstName}
      />
      <InputField
        icon={<FaIdCard />}
        id="last-name"
        name="lastName"
        type="text"
        placeholder="last name"
        value={formData.lastName}
        onChange={handleChange}
        error={fieldErrors?.lastName}
      />
      <InputField
        icon={<MdEmail />}
        id="email"
        name="email"
        type="email"
        placeholder="email"
        value={formData.email}
        onChange={handleChange}
        error={fieldErrors?.email}
      />
      <InputField
        icon={<FaLock />}
        id="plain-password"
        name="plainPassword"
        type="password"
        placeholder="password"
        value={formData.plainPassword}
        onChange={handleChange}
        error={fieldErrors?.plainPassword}
      />
      <InputField
        icon={<FaAt />}
        id="username"
        name="username"
        type="text"
        placeholder="username"
        value={formData.username}
        onChange={handleChange}
        error={fieldErrors?.username}
      />

      <div className="submit-container">
        <button type="submit" className="submit" disabled={loading}>Sign Up</button>
      </div>
    </form>
  );
};