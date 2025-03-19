import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import ReadingListUpdate from './reading-list-update.vue';
import ReadingListService from './reading-list.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';

type ReadingListUpdateComponentType = InstanceType<typeof ReadingListUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const readingListSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ReadingListUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('ReadingList Management Update Component', () => {
    let comp: ReadingListUpdateComponentType;
    let readingListServiceStub: SinonStubbedInstance<ReadingListService>;

    beforeEach(() => {
      route = {};
      readingListServiceStub = sinon.createStubInstance<ReadingListService>(ReadingListService);
      readingListServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          readingListService: () => readingListServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(ReadingListUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(ReadingListUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.readingList = readingListSample;
        readingListServiceStub.update.resolves(readingListSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(readingListServiceStub.update.calledWith(readingListSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        readingListServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ReadingListUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.readingList = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(readingListServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        readingListServiceStub.find.resolves(readingListSample);
        readingListServiceStub.retrieve.resolves([readingListSample]);

        // WHEN
        route = {
          params: {
            readingListId: `${readingListSample.id}`,
          },
        };
        const wrapper = shallowMount(ReadingListUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.readingList).toMatchObject(readingListSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        readingListServiceStub.find.resolves(readingListSample);
        const wrapper = shallowMount(ReadingListUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
