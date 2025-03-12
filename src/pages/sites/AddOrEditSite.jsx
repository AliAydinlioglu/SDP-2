import useSWR from 'swr';
import SiteForm from '../../components/sites/SiteForm';
import AsyncData from '../../components/AsyncData';
import useSWRMutation from 'swr/mutation';
import { useParams } from 'react-router-dom';
import { save, getById } from '../../api';

export default function AddOrEditSite() {
  const { id } = useParams();

  const { trigger: saveSite, error: saveError } = useSWRMutation(
    'sites',
    save,
  );

  const {
    data: site,
    error: siteError,
    isLoading: siteLoading,
  } = useSWR(id ? `sites/${id}` : null, getById);

  return (
    <>
      <h1>{ id ? 'Edit site' : 'Add site'}</h1>

      <AsyncData 
        error={saveError || siteError} 
        loading={siteLoading}
      >
        <SiteForm 
          site={site} 
          saveSite={saveSite}
        />
      </AsyncData>
    </>
  );
}