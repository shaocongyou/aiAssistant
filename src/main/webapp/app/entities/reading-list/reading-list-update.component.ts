import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ReadingListService from './reading-list.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import { type IReadingList, ReadingList } from '@/shared/model/reading-list.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ReadingListUpdate',
  setup() {
    const readingListService = inject('readingListService', () => new ReadingListService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const readingList: Ref<IReadingList> = ref(new ReadingList());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'zh-cn'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveReadingList = async readingListId => {
      try {
        const res = await readingListService().find(readingListId);
        res.createdAt = new Date(res.createdAt);
        readingList.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.readingListId) {
      retrieveReadingList(route.params.readingListId);
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
      title: {
        required: validations.required('本字段不能为空.'),
        maxLength: validations.maxLength('本字段最大长度为 100 个字符.', 100),
      },
      status: {
        required: validations.required('本字段不能为空.'),
      },
      startDate: {},
      endDate: {},
      createdAt: {
        required: validations.required('本字段不能为空.'),
      },
      user: {},
    };
    const v$ = useVuelidate(validationRules, readingList as any);
    v$.value.$validate();

    return {
      readingListService,
      alertService,
      readingList,
      previousState,
      isSaving,
      currentLanguage,
      users,
      v$,
      ...useDateFormat({ entityRef: readingList }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.readingList.id) {
        this.readingListService()
          .update(this.readingList)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A ReadingList is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.readingListService()
          .create(this.readingList)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A ReadingList is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
