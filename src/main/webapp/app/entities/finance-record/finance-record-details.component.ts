import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import FinanceRecordService from './finance-record.service';
import { useDateFormat } from '@/shared/composables';
import { type IFinanceRecord } from '@/shared/model/finance-record.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'FinanceRecordDetails',
  setup() {
    const dateFormat = useDateFormat();
    const financeRecordService = inject('financeRecordService', () => new FinanceRecordService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const financeRecord: Ref<IFinanceRecord> = ref({});

    const retrieveFinanceRecord = async financeRecordId => {
      try {
        const res = await financeRecordService().find(financeRecordId);
        financeRecord.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.financeRecordId) {
      retrieveFinanceRecord(route.params.financeRecordId);
    }

    return {
      ...dateFormat,
      alertService,
      financeRecord,

      previousState,
    };
  },
});
