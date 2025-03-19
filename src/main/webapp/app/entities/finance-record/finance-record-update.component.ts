import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import FinanceRecordService from './finance-record.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import { FinanceRecord, type IFinanceRecord } from '@/shared/model/finance-record.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'FinanceRecordUpdate',
  setup() {
    const financeRecordService = inject('financeRecordService', () => new FinanceRecordService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const financeRecord: Ref<IFinanceRecord> = ref(new FinanceRecord());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'zh-cn'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveFinanceRecord = async financeRecordId => {
      try {
        const res = await financeRecordService().find(financeRecordId);
        res.createdAt = new Date(res.createdAt);
        financeRecord.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.financeRecordId) {
      retrieveFinanceRecord(route.params.financeRecordId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      description: {
        required: validations.required('本字段不能为空.'),
        maxLength: validations.maxLength('本字段最大长度为 200 个字符.', 200),
      },
      amount: {
        required: validations.required('本字段不能为空.'),
      },
      category: {
        required: validations.required('本字段不能为空.'),
        maxLength: validations.maxLength('本字段最大长度为 50 个字符.', 50),
      },
      date: {
        required: validations.required('本字段不能为空.'),
      },
      createdAt: {
        required: validations.required('本字段不能为空.'),
      },
      user: {},
    };
    const v$ = useVuelidate(validationRules, financeRecord as any);
    v$.value.$validate();

    return {
      financeRecordService,
      alertService,
      financeRecord,
      previousState,
      isSaving,
      currentLanguage,
      users,
      v$,
      ...useDateFormat({ entityRef: financeRecord }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.financeRecord.id) {
        this.financeRecordService()
          .update(this.financeRecord)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A FinanceRecord is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.financeRecordService()
          .create(this.financeRecord)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A FinanceRecord is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
