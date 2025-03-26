import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router';

const validationRules = {
  naam: {
    required: 'Site name is required',
  },
  verantw_id: {
    required: 'Manager is required',
  },
};

const EMPTY_SITE = {
  id: undefined,
  naam: '',
  verantw_id: '',
};

export default function SiteForm({ managers = [], site = EMPTY_SITE, saveSite }) {
  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors, isValid } } = useForm({
    mode: 'onBlur',
    defaultValues: {
      naam: site?.naam,
      verantw_id: site?.verantw_id,
    },
  });

  const onSubmit = async (values) => {
    if (!isValid) return;
    console.log(values);
    
    await saveSite({
      id: site?.id,
      ...values,
      verantw_id: parseInt(values.verantw_id, 10),
    }, {
      throwOnError: false,
      onSuccess: () => navigate('/sites'),
    });
  };

  return (
    <div className="form-container">
      <form onSubmit={handleSubmit(onSubmit)} className="site-form">
        <div className="mb-3">
          <label htmlFor="naam" className="form-label">
            Site Name
          </label>
          <input
            {...register('naam', validationRules.naam)}
            id="naam"
            name="naam"
            type="text"
            className="form-control"
          />
          {errors.naam && <p className="form-text text-danger">{errors.naam.message}</p>}
        </div>

        <div className="mb-3">
          <label htmlFor="verantw_id" className="form-label">
            Manager
          </label>
          <select
            {...register('verantw_id', validationRules.verantw_id)}
            id="verantw_id"
            name="verantw_id"
            className="form-select"
          >
            <option value="" disabled>
              -- Select a manager --
            </option>
            {managers.map(({ id, voornaam, achternaam }) => (
              <option key={id} value={id}>
                {voornaam} {achternaam}
              </option>
            ))}
            
          </select>
          {errors.verantw_id && <p className="form-text text-danger">{errors.verantw_id.message}</p>}
        </div>

        <div className="clearfix">
          <div className="btn-group float-end">
            <button type="submit" className="btn btn-primary">
              {site?.id ? 'Save Site' : 'Add Site'}
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}